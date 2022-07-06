import Vue from 'vue'
import App from './App'
import store from './store'
import uView from "uview-ui";


Vue.use(uView);

Vue.config.productionTip = false

App.mpType = 'app'
Vue.prototype.$store = store
const app = new Vue({
	store,
    ...App
})

import httpInterceptor from '@/common/http.interceptor.js'

Vue.use(httpInterceptor, app)

app.$mount()
