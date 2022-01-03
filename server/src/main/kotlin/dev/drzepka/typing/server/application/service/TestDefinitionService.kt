package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.DomainTestDefinitionService
import dev.drzepka.typing.server.application.dto.testdefinition.CreateTestDefinitionRequest
import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource
import dev.drzepka.typing.server.application.dto.testdefinition.UpdateTestDefinitionRequest
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.util.Logger
import java.time.Duration
import java.time.Instant

/**
 * Manages test definitions.
 */
class TestDefinitionService(
    private val domainTestDefinitionService: DomainTestDefinitionService,
    private val testDefinitionRepository: TestDefinitionRepository,
    private val testRepository: TestRepository,
    private val wordListRepository: WordListRepository
) {
    private val log by Logger()

    fun createTestDefinition(request: CreateTestDefinitionRequest): TestDefinitionResource {
        log.info("Creating test definition {}", request)
        validateCreateTestDefinition(request)

        val definition = TestDefinition()
        definition.name = request.name
        definition.wordList = wordListRepository.findById(request.wordListId)!!
        definition.duration = request.duration?.let { Duration.ofSeconds(it.toLong()) }
        definition.isActive = request.isActive

        postValidateTestDefinition(definition)
        testDefinitionRepository.save(definition)
        log.info("Created test definition {}", definition.id)
        return TestDefinitionResource.fromEntity(definition)
    }

    fun listTestDefinitions(active: Boolean? = null): Collection<TestDefinitionResource> {
        return testDefinitionRepository.findAll(active)
            .map { TestDefinitionResource.fromEntity(it) }
    }

    fun getTestDefinition(id: Int): TestDefinitionResource? {
        return testDefinitionRepository.findById(id)
            ?.let { TestDefinitionResource.fromEntity(it) }
    }

    fun updateTestDefinition(request: UpdateTestDefinitionRequest): TestDefinitionResource? {
        log.info("Updating test definition {}", request)
        validateUpdateTestDefinition(request)
        val definition = testDefinitionRepository.findById(request.id) ?: return null

        if (request.name != null)
            definition.name = request.name!!

        if (request.duration != null)
            definition.duration = Duration.ofSeconds(request.duration!!.toLong())

        if (request.wordListId != null)
            definition.wordList = wordListRepository.findById(request.wordListId!!)!!

        if (request.isActive != null)
            definition.isActive = request.isActive!!

        postValidateTestDefinition(definition)
        testDefinitionRepository.save(definition)
        return TestDefinitionResource.fromEntity(definition)
    }

    fun deleteTestDefinition(testDefinitionId: Int): Boolean {
        log.info("Deleting test {}", testDefinitionId)
        val testCount = testRepository.countByTestDefinitionId(testDefinitionId)

        if (testCount > 0) {
            log.info("Test definition {} was solved {} times, setting the deletion date", testDefinitionId, testCount)
            val definition = testDefinitionRepository.findById(testDefinitionId) ?: return false

            definition.deletedAt = Instant.now()
            testDefinitionRepository.save(definition)
            return true
        } else {
            log.info("Test definition {} wasn't solved, it will be permanently deleted", testDefinitionId)
            return testDefinitionRepository.delete(testDefinitionId)
        }
    }

    private fun validateCreateTestDefinition(request: CreateTestDefinitionRequest) {
        val state = ValidationState()
        validateName(request.name, state, true)
        if (request.duration != null)
            validateDuration(request.duration!!, state)
        validateWordListId(request.wordListId, state)

        state.verify()
    }

    private fun validateUpdateTestDefinition(request: UpdateTestDefinitionRequest) {
        val state = ValidationState()
        if (request.name != null)
            validateName(request.name!!, state, false)
        if (request.duration != null)
            validateDuration(request.duration!!, state)
        if (request.wordListId != null)
            validateWordListId(request.wordListId!!, state)

        state.verify()
    }

    private fun validateName(name: String, state: ValidationState, checkUniqueness: Boolean) {
        if (name.isEmpty())
            state.addFieldError("name", "Name cannot be empty.")
        if (checkUniqueness && name.isNotEmpty() && testDefinitionRepository.findByName(name) != null)
            state.addFieldError("name", "Test with this name already exists.")
    }

    private fun validateDuration(duration: Int, state: ValidationState) {
        if (duration < 10)
            state.addFieldError("duration", "Duration must be at least 10 seconds.")
    }

    private fun validateWordListId(wordListId: Int, state: ValidationState) {
        if (wordListRepository.findById(wordListId) == null)
            state.addFieldError("wordListId", "Word list with given id doesn't exist.")
    }

    private fun postValidateTestDefinition(testDefinition: TestDefinition) {
        domainTestDefinitionService.checkIfFixedTextIsLongEnough(testDefinition)
    }
}