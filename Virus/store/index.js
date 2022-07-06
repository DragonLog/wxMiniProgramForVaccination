import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

//读取本地信息
const userInfo=uni.getStorageSync('userInfo');
let userLogined;


if(userInfo){
	userLogined=userInfo;
	
}else{
	userLogined = {
		token: ''
	}
	
}
const store = new Vuex.Store({
	state: {		
		userLogined
	},
	mutations: {
		login(state,res) {
			  state.userLogined=res;
			  uni.setStorageSync("userInfo",res);
		},
		logout(state) {

			state.userLogined={token:''};
			uni.setStorageSync('userInfo','');
			// uni.setStorageSync("userType","");

		}
		
	},
	getters: {
		
	},
	actions: {
		
	}
})

export default store
