import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import TestPage from "@/views/typing/TestPage.vue";
import Login from '@/views/Login.vue';
import Settings from "@/views/settings/Settings.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        redirect: {name: 'TestPage'}
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/test',
        name: 'TestPage',
        component: TestPage
    },
    {
        path: '/settings',
        name: 'Settings',
        component: Settings
    }
];

const router = new VueRouter({
    routes
});

export default router
