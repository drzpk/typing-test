<template>
    <div id="loader">
        <div id="loader-content">
            <LoadingSpinner/>
            <span>Loading...</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import LoadingSpinner from "@/views/shared/LoadingSpinner.vue";

    @Component({
        components: {LoadingSpinner},
        mounted() {
            this.$store.dispatch("loadApplication");
        }
    })
    export default class Loading extends Vue {
    }
</script>

<style lang="scss" scoped>
    #loader {
        position: fixed;
        width: 100%;
        height: 100%;
    }

    #loader-content {
        display: flex;
        height: 100%;
        justify-content: center;
        align-items: center;
    }

    span {
        margin-left: 20px;
        font-size: 2em;
    }
</style>