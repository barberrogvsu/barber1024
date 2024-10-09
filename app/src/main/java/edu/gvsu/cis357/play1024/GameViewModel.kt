package edu.gvsu.cis357.play1024

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.floor

class GameViewModel: ViewModel() {
    private val _numbers = MutableLiveData<List<String>>()
    val numbers: LiveData<List<String>> get() = _numbers
    var size = 4;
    var target = 1024;
    val addNumbers = arrayOf<String>("1", "2", "4");

    var validSwipes = 0;

    var gameState = State.ACTIVE;
    var lastSwiped: Swipe? = null;



    init {
        val newList = MutableList(size*size) { "N/A" }
        // populate
        for (x in 0..<size) {
            for (y in 0..<size) {

                val pos = Pair(x, y)
                val index = x + (y * size)
                val value = _numbers.value?.get(index)
                if (value != null) {
                    newList[index] = value
                }
                else {
                    newList[index] = ""
                }

            }
        }

        addRandomValue(newList, "2")
        _numbers.value = newList

    }

    fun resetGame() {
        val newList = MutableList(size*size) { "" }
        addRandomValue(newList, "2")
        _numbers.value = newList

        lastSwiped = null
        gameState = State.ACTIVE

        validSwipes = 0
    }
    fun doSwipe(dir: Swipe) {

        if (gameState != State.ACTIVE) {return}

        val newList = MutableList(size*size) { "N/A" }

        // populate
        for (x in 0..<size) {
            for (y in 0..<size) {

                val pos = Pair(x, y)
                val index = x + (y * size)
                val value = _numbers.value?.get(index)
                if (value != null) {
                    newList[index] = value
                }
                else {
                    newList[index] = ""
                }

            }
        }

        var boardChanges = 0
        // do thing
        for (_i0 in 0..<size) {
            for (_i1 in 0..<size) {

                val offset = offsetPos(Pair(0, 0), dir)

                var x = 0
                var y = 0

                if (dir == Swipe.UP) {
                    // iterate over top first
                    x = _i1
                    y = _i0
                }
                else if (dir == Swipe.RIGHT) {
                    // iterate over right column first
                    x = (size-1)-_i0
                    y = _i1

                }
                else if (dir == Swipe.DOWN) {
                    // iterate over bottom row first
                    x = _i1
                    y = (size-1)-_i0
                }
                else /*if (dir == Swipe.LEFT)*/ {
                    // iterate over left column
                    x = _i0
                    y = _i1
                }

                val pos = Pair(x, y)
                val value = getTile(newList, pos)

                val distance = 0

                if (value != "") {

                    val movePos = slidePos(newList, pos, dir, value)

                    if (movePos != pos) {

                        if (posValid(movePos)) {
                            val replaceTile = getTile(newList, movePos)
                            if (replaceTile == "") {
                                boardChanges += 1

                                setTile(newList, pos, "")
                                setTile(newList, movePos, value)
                            } else if (replaceTile == value) {
                                boardChanges += 1
                                val newValue = (value.toInt() * 2).toString()
                                setTile(newList, pos, "")
                                setTile(newList, movePos, newValue)
                            }

                        }

                    }

                }

            }

        }

        if (boardChanges > 0 ) {
            validSwipes += 1
            addRandomValue(newList, addNumbers.random())
            lastSwiped = dir
        }


        if (checkFail(newList)) {
            gameState = State.LOST
        }
        if (checkWin(newList)) {
            gameState = State.WON
        }


        _numbers.value = newList

    }



    fun addRandomOne(list: MutableList<String>): MutableList<String> {
        addRandomValue(list, "1")
        return list
    }

    fun addRandomValue(list: MutableList<String>, value: String): MutableList<String> {

        var filledSpaces = 0
        val maxSize = (size*size)

        while (true) {

            val xx = floor(Math.random() * size).toInt()
            val yy = floor(Math.random() * size).toInt()

            val pos = Pair(xx, yy)
            val thisTile = getTile(list, pos)

            if (thisTile != "") {
                filledSpaces += 1
            }
            else if (posValid(pos)) {
                setTile(list, pos, value)
                return list
            }

            if (filledSpaces >= maxSize) {return list}

        }

    }

    fun checkFail(list: MutableList<String>): Boolean {

        for (x in 0..<size) {
            for (y in 0..<size) {

                val pos = Pair(x, y)
                val value = getTile(list, pos)

                if (value == "") {
                    println("  found empty tile at "+pos)
                    return false
                }

                for (d in Swipe.entries) {
                    val checkPos = offsetPos(pos, d)
                    if (posValid(checkPos)) {
                        val checkValue = getTile(list, checkPos)
                        if (checkValue == value) {
                            println("  found similar tile <$value> at "+pos+" "+offsetPos(pos, d))
                            return false
                        }
                    }
                }

            }
        }

        // only once all possible reasons you're NOT in a fail state, will it deduce you've failed
        return true

    }



    fun checkWin(list: MutableList<String>): Boolean {

        for (x in 0..<size) {
            for (y in 0..<size) {
                if (getTile(list, Pair(x, y)) == target.toString()) {
                    return true
                }
            }
        }

        return false
    }

//    offset a pair of ints by a swipe direction
    fun offsetPos(pos: Pair<Int, Int>, dir: Swipe, amount:Int=1): Pair<Int, Int>  {

        if (dir == Swipe.UP)    {return Pair(pos.first, pos.second-amount)}
        if (dir == Swipe.DOWN)  {return Pair(pos.first, pos.second+amount)}
        if (dir == Swipe.RIGHT) {return Pair(pos.first+amount, pos.second)}
        if (dir == Swipe.LEFT)  {return Pair(pos.first-amount, pos.second)}
        return pos;

    }

    fun safeOffset(pos: Pair<Int, Int>, dir: Swipe, amount:Int=1): Pair<Int, Int>  {

        if (dir == Swipe.UP)    {return Pair(pos.first, pos.second-amount)}
        if (dir == Swipe.DOWN)  {return Pair(pos.first, pos.second+amount)}
        if (dir == Swipe.RIGHT) {return Pair(pos.first+amount, pos.second)}
        if (dir == Swipe.LEFT)  {return Pair(pos.first-amount, pos.second)}
        return pos;

    }

    fun slidePos(list: MutableList<String>, pos: Pair<Int, Int>, dir: Swipe, value: String): Pair<Int, Int>  {

        val checkPos = offsetPos(pos, dir, 1)

        if (!posValid(checkPos)) {
            return pos
        }
        else {

            val checkValue = getTile(list, checkPos)
            println("  $checkValue")
            if (checkValue == "") {
                return slidePos(list, checkPos, dir, value)
            }
            else if (checkValue == value) {
                return checkPos
            }

        }
        return pos

    }

    fun posValid(pos: Pair<Int, Int>): Boolean {
        return (pos.first >= 0) and (pos.first < size) and (pos.second >= 0) and (pos.second < size);
    }

    fun setTile(list: MutableList<String>, coord: Pair<Int, Int>, value: String): MutableList<String> {
        val index = coord.first + (coord.second*size)
        list[index] = value
        return list
    }

    fun getTile(list: List<String>, coord: Pair<Int, Int>): String {
        if (!posValid(coord)) {return "9999999999"}
        val index = coord.first + (coord.second*size)
        return list[index]
    }

    fun safeGet(list: List<String>, coord: Pair<Int, Int>): String {
        if (!posValid(coord)) {return "9999999999"}
        val index = coord.first + (coord.second*size)
        return list[index]
    }


}