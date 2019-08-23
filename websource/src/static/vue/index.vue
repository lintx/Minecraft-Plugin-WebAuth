<template>
    <div class="cover-container d-flex h-100 p-3 mx-auto flex-column">
        <header class="masthead mb-auto">
            <div class="inner">
                <h3 class="masthead-brand">WebAuth</h3>
                <nav class="nav nav-masthead justify-content-center">
                    <router-link class="nav-link" exact-active-class="active" to="/">主页</router-link>
                    <router-link class="nav-link" exact-active-class="active" to="/help">帮助</router-link>
                </nav>
            </div>
        </header>

        <main role="main" class="inner cover">
            <router-view></router-view>
        </main>

        <footer class="mastfoot mt-auto">
            <div class="inner">
                <p>Author:LinTx</p>
            </div>
        </footer>
    </div>
</template>