<template>
    <div class="search-field">
        <div class="col-1">
            <input v-model.trim="searchString" placeholder="Search by name">
        </div>
        <div class="col-2">
            <button type="button" v-on:click="changeSort">
                {{ sort }}
            </button>
        </div>
    </div>
</template>

<script>
export default {
    name: "SearchBox",
    data: function() {
        return {
            searchString: "",
            sortAsc: true,
        }
    },
    watch: {
        searchString: function(val) {
            this.$emit('searchingContact', val);
        }
    },
    computed: {
        sort: function() {
            if (this.sortAsc) {
                return "Sort DESC ↑";
            } 
            return "Sort ASC ↓";
        }
    },
    methods: {
        changeSort: function() {
            if (this.sortAsc) {
                this.sortAsc = false;
            } else {
                this.sortAsc = true;
            }
            this.$emit('changeSort', this.sortAsc);
        }
    }

}
</script>

<style scoped>
.search-field input {
    padding: 0.2em;
    font-size: 1em;
    border-radius: 40px;
    border: 1px solid lightblue;
    padding: 10px;
}
.search-field button {
    font-size: 12px;
    padding: 4px 10px;
    border: 0;
    background: lightblue;
    border-radius: 40px;
    height: 40px;
    margin-left: 3px;
}
.col-1, .col-2 {
    display: inline-block;
    vertical-align: middle;
}
.col-1 {
    width: 70%;
}
.col-2 {
    width: 30%;
}
</style>