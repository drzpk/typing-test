<template>
    <header>
        <h1>Typing Test</h1>
        <div id="user-panel">
            <span>User Name</span>
            <div>
                <font-awesome-icon icon="cog"/>
                <span>Settings</span>
            </div>
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
    }
</style>