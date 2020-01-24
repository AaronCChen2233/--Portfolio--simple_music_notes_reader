package com.example.simplemusicnotesreader.models

import com.example.simplemusicnotesreader.enums.Tie
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.lang.Exception


fun getDivisions(barattr: NodeList): Int {
    var divisions = getNodeValue("divisions", barattr.item(0) as Element)
    return if (divisions != "") divisions.toInt() else 0
}

fun getTimeSignature(barattr: NodeList): String {
    val best = getNodeValue("beats", barattr.item(0) as Element)
    val beatType = getNodeValue("beat-type", barattr.item(0) as Element)
    return "$best/$beatType"
}

fun getkeySignature(barattr: NodeList) = getNodeValue("sign", barattr.item(0) as Element)


fun getNotes(measure: Node, divisions: Int): ArrayList<note> {

    val notes: ArrayList<note> = ArrayList<note>()

    val nList = (measure as Element).getElementsByTagName("note")
    for (i in 0..nList.length - 1) {
        try {
            val note = nList.item(i)
            val pitch = note.ownerDocument.getElementsByTagName("pitch").item(0) as Element
            val step = getNodeValue("step", pitch)
            val octave = getNodeValue("octave", pitch)

            var tie = Tie.Non
            val tieNode = (note as Element).getElementsByTagName("tie")
            if (tieNode.length > 1) {
                tie = Tie.Both
            } else if (tieNode.length == 1) {
                val tieString = tieNode.item(0).attributes.getNamedItem("type").nodeValue
                tie = if (tieString == "start") Tie.Start else Tie.Stop
            }

            /**duration should convert in JavaScript*/
            val duration = getNodeValue("duration", note as Element).toFloat() / divisions
            val key = "$step/$octave"

            notes.add(note(key, duration, tie))

        } catch (e: Exception) {
            print(i.toString())
        }
    }
    return notes
}

fun getTies(notes: ArrayList<note>): ArrayList<tie> {
    var ties = ArrayList<tie>()
    var startIndex = 0
    var stopIndex = 0

    for (i in 0..notes.size - 1) {
        when (notes.get(i).tie) {
            Tie.Start ->
                startIndex = i

            Tie.Stop -> {
                stopIndex = i
                ties.add(tie(startIndex, stopIndex))
            }

            Tie.Both -> {
                stopIndex = i
                ties.add(tie(startIndex, stopIndex))
                startIndex = i
            }
        }
    }

    return ties
}

fun xmldocListCorvertTobarDataList(docs: NodeList): ArrayList<barData> {
    var division = 0
    var timeSignation = ""
    var keySignation = ""
    val bars = ArrayList<barData>()
    for (i in 0..docs.length - 1) {
        val measure = docs.item(i)

        /**attributes part*/
        val barattr = (measure as Element).getElementsByTagName("attributes")
        if (barattr.length != 0) {
            val newDivision = getDivisions(barattr)
            division = if(newDivision!=0) newDivision else division
            timeSignation = getTimeSignature(barattr)
            keySignation = getkeySignature(barattr)
        } else {
            timeSignation = ""
            keySignation = ""
        }

        val notes = getNotes(measure, division)

        val bar = barData(timeSignation, keySignation, notes, getTies(notes), 500, 500, 60)
        bars.add(bar)
    }
    return bars
}

fun getNodeValue(tag: String, element: Element): String {
    val nodeList = element.getElementsByTagName(tag)
    val node = nodeList.item(0)
    if (node != null) {
        if (node.hasChildNodes()) {
            val child = node.getFirstChild()
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE) {
                    return child.getNodeValue()
                }
            }
        }
    }
    return ""
}
