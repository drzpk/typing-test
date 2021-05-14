import Vue from 'vue'
import VueRouter, {RouteConfig} from 'vue-router'
import TestPage from "@/views/typing/TestPage.vue";
import Login from '@/views/Login.vue';
import Settings from "@/views/settings/Settings.vue";
import AdminSettings from "@/views/settings/admin/AdminSettings.vue";
import WordList from "@/views/settings/admin/word/WordList.vue";
import TestDefinition from "@/views/settings/admin/test/TestDefinition.vue";

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
    },
    {
        path: '/settings/admin',
        name: 'Admin settings',
        component: AdminSettings
    },
    {
        path: '/settings/admin/word-lists/new',
        name: 'New word list',
        component: WordList
    },
    {
        path: '/settings/admin/word-lists/:id',
        name: 'Word list details',
        component: WordList
    },
    {
        path: '/settings/admin/test-definitions/:id',
        name: 'Test definition details',
        component: TestDefinition
    }
];

const router = new VueRouter({
    routes
});

export default router
