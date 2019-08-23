<script>
    import openPage from "./openRegister.vue";
    import closePage from "./closeRegister.vue";
    import Session from "../js/libs/session";

    export default {
        data(){
            return {
                canRegister:true
            }
        },
        render(createElement, context) {
            if (this.canRegister){
                return createElement(openPage);
            }else {
                return createElement(closePage);
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