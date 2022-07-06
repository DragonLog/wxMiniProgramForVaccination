<template>
	<view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			}
		},
		onLoad() {
			this.init();
		},
		methods: {
			init(){
				const token=this.$store.state.userLogined.token;
				const userType=this.$store.state.userLogined.userType
				if(token){
					this.$u.post("/validate",{token,userType}).then((res)=>{
						if(res.code == 0){
							uni.switchTab({
								url:"main"
							})
						}else{
							uni.reLaunch({
							    url: "../user/login"
								
							})
						}
					})
				}else{
					uni.reLaunch({
					    url: "../user/login"
						
					})
				}
				
			}

		}
	}
</script>

<style>

</style>
