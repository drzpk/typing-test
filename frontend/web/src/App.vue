<template>
    <div id="app">
        <div v-if="applicationLoaded">
            <Header/>
            <router-view/>
        </div>
        <div v-if="!applicationLoaded">
            <Loading/>
        </div>
    </div>
</template>

<script lang="ts">
import Header from "@/views/shared/Header.vue";
import {Component, Vue} from "vue-property-decorator";
import {mapState} from "vuex";
import Loading from "@/views/Loading.vue";

@Component({
    components: {Loading, Header},
    computed: mapState([
        "applicationLoaded",
        "pendingRequest"
    ])
})
export default class App extends Vue {
    applicationLoaded!: boolean;
    pendingRequest!: boolean;
}
</script>

<style lang="scss">
@import "styles/LoadingOverlay";

html, body {
    margin: 0;
    width: 100%;
}

#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
}

#nav {
    padding: 30px;

    a {
        font-weight: bold;
        color: #2c3e50;

        &.router-link-exact-active {
            color: #42b983;
        }
    }
}

.card {
    margin: 1.5em 0;
}
</style>