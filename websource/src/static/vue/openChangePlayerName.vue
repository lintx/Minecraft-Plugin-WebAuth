<template>
    <form class="form-signin" @submit="changePlayerName">
        <div class="text-center mb-4">
            <h1 class="cover-heading">修改玩家名</h1>
        </div>

        <div class="mb-4">
            <div class="form-label-group">
                <input v-model="playerName" type="text" id="register_password" class="form-control" placeholder="玩家名" required>
                <label for="register_password">玩家名</label>
            </div>
        </div>

        <button class="btn btn-lg btn-primary btn-block" type="submit">修改玩家名</button>
    </form>
</template>

<script>
    import Session from "../js/libs/session";

    export default {
        data(){
            return {
                playerName:""
            }
        },
        methods:{
            changePlayerName(event){
                event.preventDefault();
                let vm = this;
                Session.changePlayerName(this.playerName,()=>{
                    vm.$router.push("/");
                });
            }
        },
        beforeCreate() {
            this.$nextTick(function () {
                this.playerName = Session.getPlayerData().playerName;
            });
        }
    }
</script>