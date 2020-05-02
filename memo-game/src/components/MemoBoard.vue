<template>
    <div class="memo-board">
        <MemoField
            v-for="field in fields"
            v-bind:key="field.id"
            v-bind:id="field.id"
            v-bind:front="field.front"
            v-bind:back="field.back"
            v-bind:symbol="field.symbol"
            v-bind:isFlipped="field.isFlipped"
            v-bind:isVisible="field.isVisible"
            v-on:clickedOnField="reverse"
        ></MemoField>
        <div class="button-wrapper">
            <button class="button-restart" v-bind:class="{hidden: !isRestartVisible}" v-on:click="$emit('generateNewBoard')">Restart</button>
        </div>
    </div>
</template>

<script>
import MemoField from './MemoField.vue'

export default {
    name: 'MemoBoard',
    components: {
        MemoField,
    },
    props: {
        fields: Array,
        selected: Array,
        isRestartVisible: Boolean,
    },
    methods: {
        reverse: function(val) {
            this.$emit('clickedOnField', val);
        }
    }
}
</script>

<style scoped>
.memo-board {
    clear:both;
}
.hidden {
    opacity: 0;
}
.button-wrapper {
    text-align: center;
    padding: 20px;
}
.button-restart {
    padding: 1em;
    font-size: 2em;;
    background: blueviolet;
}
</style>