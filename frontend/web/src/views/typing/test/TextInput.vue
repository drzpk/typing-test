<template>
    <div id="text-input" class="test-panel-font">
        <!--suppress HtmlFormInputWithoutLabel -->
        <input type="text">
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component({
        data: () => {
            return {
                wordCount: [...Array(20).keys()]
            }
        }
    })
    export default class TextInput extends Vue {
    }
</script>

<style lang="scss" scoped>
    #text-input {
        margin-top: 1em;
        padding: 0.08em;
        background-color: #a0a0a0;
    }

    input {
        width: 60%;
    }
</style>