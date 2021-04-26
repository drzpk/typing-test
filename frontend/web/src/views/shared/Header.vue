<template>
    <header>
        <h1>Typing Test</h1>
        <div id="user-panel">
            <router-link to="/settings">
                <span>User Name</span>
            </router-link>
        </div>
    </header>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class Header extends Vue {

    }
</script>

<style lang="scss" scoped>
    @import "../../styles/colors.scss";
    header {
        height: 4em;
        background-color: $header-background;
    }

    h1 {
        float: left;
        margin: 0;
    }

    #user-panel {
        float: right;
        color: $header-text;

        > a > span {
            color: white;
        }
    }
</style>