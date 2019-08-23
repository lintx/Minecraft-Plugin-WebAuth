<template>
    <div class="text-center mb-4">
        <h1 class="cover-heading">我的资料</h1>
        <div class="row mt-5">
            <div class="col-4 text-right">ID</div>
            <div class="col-8 text-left"><span class="badge badge-success">{{userId}}</span></div>
        </div>
        <div class="row">
            <div class="col-4 text-right">玩家名</div>
            <div class="col-8 text-left"><span class="badge badge-success">{{playerName}}</span><router-link v-if="canChangePlayername" to="/changeUserName">修改</router-link></div>
        </div>
        <div class="row">
            <div class="col-4 text-right">登录凭据</div>
            <div class="col-8 text-left"><span class="badge badge-success">{{userToken}}</span><a @click="refresh" href="javascript:void(0)">刷新</a></div>
        </div>
        <div class="row mb-5">
            <div class="col-4 text-right">过期时间</div>
            <div class="col-8 text-left"><span class="badge badge-success">{{userTokenTime}}</span></div>
        </div>

        <router-link class="btn btn-secondary btn-block" to="/changePassword">修改密码</router-link>
        <button class="btn btn-secondary btn-block" @click="loginout">退出登录</button>
    </div>
</template>

<script>
    import Session from "../js/libs/session"
    export default {
        data(){
            return {
                playerName:"",
                userId:0,
                userToken:"登录凭据已隐藏",
                userTokenTime: "",
                canChangePlayername:false
            };
        },
        methods:{
            loginout(){
                Session.loginout();
                this.$router.push("/login");
            },
            refresh(){
                let vm = this;
                Session.refresh(()=>{
                    let d = Session.getPlayerData();
                    vm.userToken = d.userToken;
                    vm.userTokenTime = d.userTokenTime;
                });
            }
        },
        beforeCreate() {
            this.$nextTick(function () {
                let d = Session.getPlayerData();
                this.playerName = d.playerName;
                this.userId = d.userId;
                this.userToken = d.userToken;
                this.userTokenTime = d.userTokenTime;
                let vm = this;
                Session.getBaseData((data) => {
                    vm.canChangePlayername = data.canChangePlayername;
                });
            });
        }
    }
</script>