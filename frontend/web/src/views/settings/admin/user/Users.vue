<template>
    <div>
        <b-table :items="users" :fields="fields" id="users-table">
            <!--suppress HtmlUnknownAttribute -->
            <template #cell(activated)="data">
                <span :class="{'user-inactive': data.item.activatedAt == null}">{{ data.item.activatedAt != null ? "yes" : "no"}}</span>
            </template>

            <!--suppress HtmlUnknownAttribute -->
            <template #cell(actions)="data">
                <b-button-group>
                    <b-button v-if="data.item.activatedAt == null" variant="success" v-b-tooltip.hover
                              title="Activate user" @click="activateUser(data.item)">
                        <font-awesome-icon icon="user-check"/>
                    </b-button>
                    <b-button v-if="!data.item.isAdmin" variant="danger" v-b-tooltip.hover title="Delete user"
                              @click="deleteUser(data.item)">
                        <font-awesome-icon icon="trash"/>
                    </b-button>
                </b-button-group>
            </template>
        </b-table>

        <b-form-checkbox v-model="inactiveOnly">Show inactive only</b-form-checkbox>
        <br>

        <b-pagination v-if="usersMetadata"
                      v-model="usersMetadata.page"
                      :total-rows="usersMetadata.totalElements"
                      :per-page="usersMetadata.size"
        />

        <b-modal id="activate-user-modal" title="Confirmation" @ok="doActivateUser" v-if="userModalTarget">
            Are you sure you want to activate user '{{userModalTarget.email}}' ({{userModalTarget.id}})?
        </b-modal>

        <b-modal id="delete-user-modal" title="Confirmation" @ok="doDeleteUser" v-if="userModalTarget">
            Are you sure you want to delete user '{{userModalTarget.email}}' ({{userModalTarget.id}})?
            All user data along with their tests will be lost. <b>This operation cannot be undone.</b>
        </b-modal>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {UserModel} from "@/models/user";
    import {PageMetadata} from "@/models/pagination";
    import {mapGetters} from "vuex";
    import DateService from "@/services/Date.service";

    @Component({
        computed: {
            ...mapGetters(["users", "usersMetadata"])
        }
    })
    export default class Users extends Vue {
        users!: UserModel[];
        usersMetadata!: PageMetadata | null;

        userModalTarget: UserModel | null = null;
        inactiveOnly = false;
        fields = [
            {
                key: "id",
                label: "Identifier"
            },
            {
                key: "email"
            },
            {
                key: "displayName"
            },
            {
                key: "createdAt",
                formatter: (date: Date) => DateService.formatDateToString(date)
            },
            {
                key: "activated",
                label: "Activated"
            },
            {
                key: "actions",
                label: "Actions"
            }
        ];

        @Watch("usersMetadata.page")
        onPageChanged(): void {
            if (this.usersMetadata)
                this.$store.dispatch("reloadUserList", this.usersMetadata.page);
        }

        @Watch("inactiveOnly")
        onInactiveOnlyChanged(): void {
            this.$store.commit("setUserInactiveOnly", this.inactiveOnly);
            this.$store.dispatch("reloadUserList");
        }

        mounted(): void {
            this.$store.dispatch("reloadUserList");
        }

        activateUser(user: UserModel): void {
            this.userModalTarget = user;
            this.$nextTick().then(() => this.$bvModal.show("activate-user-modal"));
        }

        deleteUser(user: UserModel): void {
            this.userModalTarget = user;
            this.$nextTick().then(() => this.$bvModal.show("delete-user-modal"));
        }

        doActivateUser(): void {
            if (this.userModalTarget) {
                this.$store.dispatch("activateUser", this.userModalTarget.id);
                this.userModalTarget = null;
            }
        }

        doDeleteUser(): void {
            if (this.userModalTarget) {
                this.$store.dispatch("deleteUser", this.userModalTarget.id);
                this.userModalTarget = null;
            }
        }
    }
</script>

<style lang="scss" scoped>
    .user-inactive {
        color: red;
    }
</style>