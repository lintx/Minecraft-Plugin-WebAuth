<template>
    <form class="form-signin" @submit="login">
        <div class="text-center mb-4">
            <h1 class="cover-heading">登录</h1>
        </div>

        <div class="mb-4">
            <div class="form-label-group">
                <input v-model="username" type="text" id="login_username" class="form-control" placeholder="帐号" required autofocus>
                <label for="login_username">帐号</label>
            </div>

            <div class="form-label-group">
                <input v-model="password" type="password" id="login_password" class="form-control" placeholder="密码" required>
                <label for="login_password">密码</label>
            </div>
        </div>

        <button class="btn btn-lg btn-primary btn-block" type="submit">立即登录</button>
        <router-link v-if="canRegister" class="btn btn-secondary btn-block" to="/register">没有帐号，去注册</router-link>
    </form>
</template>

<script>
    import Session from "../js/libs/session"
    export default {
        data(){
            return {
                username:"",
                password:"",
                canRegister:false
            }
        },
        methods:{
            login(event){
                event.preventDefault();
                let vm = this;
                Session.login({username:this.username,password: this.password},()=>{
                    vm.$router.push("/");
                })
            }
        },
        beforeCreate() {
            this.$nextTick(function () {
                let vm = this;
                Session.getBaseData((data) => {
                    vm.canRegister = data.canRegister;
                });
            });
        }
    }
</script>