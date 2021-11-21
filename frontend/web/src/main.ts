import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import '@/directives/LoadingOverlay'

import {library} from '@fortawesome/fontawesome-svg-core'
import {fas} from '@fortawesome/free-solid-svg-icons'
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'
import CountryFlag from 'vue-country-flag'

import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

library.add(fas);
Vue.component('font-awesome-icon', FontAwesomeIcon);
Vue.component('country-flag', CountryFlag);
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);

Vue.config.productionTip = false;

const appRoot = new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');

export default appRoot;
