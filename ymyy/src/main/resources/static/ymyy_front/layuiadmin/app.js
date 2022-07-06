// 数据服务器地址
const server_url="http://localhost:8080";
// token 名称
const token="token";
// 应用的根目录
const contextPath ="/ymyy_front";

if(location.href.indexOf("login.html")==-1){
	// 不是登录页
	var tokenStr = localStorage.getItem(token);
	if(tokenStr==null){
		console.log("token is null")
		location.href=contextPath+"/views/login.html";
	}else{
		var xhr;
		// 创建ajax引擎
		if(window.XMLHttpRequest){
			xhr = new XMLHttpRequest();
		} else if (window.ActiveXObject){
			xhr = new ActiveXObject("Microsoft.XMLHTTP");
		}
		// 指定回调函数（复写onreadystatuschange方法）
		xhr.onreadystatechange=function(){
			if(xhr.readyState==4){
				if(xhr.status==200){
					var result = xhr.responseText;
					console.log(result);
					var obj = JSON.parse(result);
					console.log(obj);
					if(obj.code != 0){
						location.href=contextPath+"/views/login.html";
					}
				}
			}
		};
		// 创建请求
		xhr.open("POST", server_url+"/validate",true);
		// 设置请求头
		xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
		// 发送请求
		var data={}		
		data.token=tokenStr;
		data.userType="admin";
		
		var stringData=JSON.stringify(data);
		console.log(stringData);
		xhr.send(stringData);

	}	
}

function getRealName(){
	var user = JSON.parse(localStorage.getItem("admin"));
	return user.realName;
}