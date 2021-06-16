<template>
    <header>
        <div id="title-container">
            <div id="title">
                <h1 @click="goToMainPage"><span class="key">T</span>yping <span class="key">T</span>est</h1>
            </div>
        </div>

        <div id="user-panel" v-show="isLoggedIn">
            <div class="button" @click="openUserPanel">
                <font-awesome-icon icon="user"/>
                User panel
            </div>
            <div class="button" @click="logout">
                <font-awesome-icon icon="sign-out-alt"/>
                Log out
            </div>
        </div>
    </header>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {mapGetters} from "vuex";

    @Component({
        computed: {
            ...mapGetters(["isLoggedIn"])
        }
    })
    export default class Header extends Vue {
        isLoggedIn!: boolean;

        goToMainPage(): void {
            if (this.isLoggedIn)
                this.$router.push("/test");
        }

        openUserPanel(): void {
            this.$router.push("/settings");
        }

        logout(): void {
            this.$store.dispatch("logout").then(() => {
                this.$router.push("/login");
            });
        }
    }
</script>

<style lang="scss" scoped>
    @import "../../styles/colors.scss";
    header {
        height: 4em;
        background-color: $header-background;
        box-shadow: #a5a5a5 -2px 1px 4px 1px;
    }

    #title-container {
        display: inline-block;
        height: 100%;

        #title {
            height: 100%;
            display: flex;
            align-items: center;

            h1 {
                float: left;
                margin: 0 0.5em;
                cursor: pointer;

                .key {
                    border: 3px solid #2c3e50;
                    border-radius: 6px;
                    padding: 0 0.3em;
                }
            }
        }
    }

    #user-panel {
        height: 100%;
        margin-right: 1em;
        display: flex;
        justify-content: center;
        flex-direction: column;
        float: right;
        color: $header-text;

        > a > span {
            color: white;
        }

        .button {
            width: 100%;
            cursor: pointer;
            padding: 0.09em;
            margin: 0.01em;
            border-radius: 5px;
            transition: border linear 0.1s;
            border-width: 1px;
            border-style: solid;
            border-color: transparent;

            &:hover {
                border-color: $header-text;
            }
        }
    }
</style>