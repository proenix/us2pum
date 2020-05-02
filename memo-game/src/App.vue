<template>
  <div id="app">
    <MemoCounter 
      :movesCounter=this.movesCounter 
      :pairsTotal=this.pairsTotal 
      :pairsLeft=this.pairsLeft
    ></MemoCounter>
    <MemoBoard 
      :fields=this.fields 
      :selected=this.selected 
      :isRestartVisible=this.isRestartVisible
      v-on:clickedOnField="reverse"
      v-on:generateNewBoard="generateNewBoard"
    ></MemoBoard>
  </div>
</template>

<script>
import MemoCounter from './components/MemoCounter.vue'
import MemoBoard from './components/MemoBoard.vue'

export default {
  name: 'App',
  components: {
    MemoBoard,
    MemoCounter,
  },
  data: function() {
    return {
      fields: [],
      selected: [],
      movesCounter: 0,
      pairsTotal: 0,
      pairsLeft: 0,
      isRestartVisible: false,
    }
  },
  watch: {
    movesCounter: function(val) {
       try {
        window.MyHandler.setResult(val);  
      } catch (error) {
        console.log("MyHandler not implemented.");
      }
    }
  },
  mounted: function() {
    this.generateNewBoard();
  },
  methods: {
    generateNewBoard: function() {
      let pairs = 8;
      let kanji = ["1", "2", "3", "4", "5", "6", "7", "8","1", "2", "3", "4", "5", "6", "7", "8"];
      for (let index = 0; index < pairs*2; index++) {
        var current = kanji.splice(Math.floor( Math.random()*kanji.length), 1)[0];
        this.fields.push({
          id: index,
          back: current + ".png",
          front: "front.png",
          isFlipped: false,
          symbol: current,
          isVisible: true,
        })
      }

      this.pairsTotal = pairs;
      this.pairsLeft = pairs;
      this.movesCounter = 0;

      this.isRestartVisible = false;
    },

    reverse: function(val) {
      if (this.fields[val].isVisible == false) {
        return;
      }
      if (this.selected.length > 1) {
        // After not winning move.
        this.fields[this.selected[0]].isFlipped = false;
        this.fields[this.selected[1]].isFlipped = false;
        this.selected = [];

      } else if (this.selected.length == 1) {
        // Second field selected.
        // If the same field is clicked do nothing.
        if (this.selected[0] == val) {
          return;
        }
        // Add to selected array, update move counter and flip.
        this.selected.push(val);
        this.fields[val].isFlipped = true;
        this.movesCounter += 1;

        // Check for win condition.
        if (this.fields[this.selected[0]].symbol == this.fields[this.selected[1]].symbol) {
          // Hide the winning fields;
          this.fields[this.selected[0]].isVisible = false;
          this.fields[this.selected[1]].isVisible = false;

          this.pairsLeft -= 1;
          this.selected = [];

          if (this.pairsLeft == 0) {
            this.fields = [];
            this.isRestartVisible = true;
          } 
        }
      } else {
        this.selected.push(val);
        this.fields[val].isFlipped = true;
      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100vh;
}
body {
  margin:0;
}
</style>
