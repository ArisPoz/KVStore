package com.arist

import java.lang.StringBuilder

/**
 * @author arist
 */
data class Trie(val root: TrieNode = TrieNode(), val fin: Boolean = false) {

    data class TrieNode
        (var fin: Boolean = false, var subNodes: MutableMap<Char, TrieNode> = HashMap())

    fun put(key: String) {
        var currentNode = root
        key.forEach {
            var node = currentNode.subNodes[it]
            if (node == null) {
                node = TrieNode()
                currentNode.subNodes[it] = node
            }
            currentNode = node
        }
        currentNode.fin = true
    }

    fun get(key: String): Pair<Type, String> {
        val currentNode = exists(key)
        if (currentNode == null || !currentNode.fin) {
            return Pair(Type.NOT_FOUND, Type.NOT_FOUND.msg)
        }

        return Pair(Type.OK, buildString(key, currentNode))
    }

    fun query(key: String): Pair<Type, String> {
        if (exists(key.split(".")[0]) == null || !exists(key.split(".")[0])?.fin!!) {
            return Pair(Type.NOT_FOUND, Type.NOT_FOUND.msg)
        }

        val currentNode = exists(key.replace('.', '-'))
        if (currentNode == null || !currentNode.fin) {
            return Pair(Type.NOT_FOUND, Type.NOT_FOUND.msg)
        }

        return Pair(Type.OK, buildString(key, currentNode, true))
    }

    fun delete(key: String): Pair<Type, String> {
        val node = exists(key)
        return if (node != null && node.fin) {
            delete(root, key, 0)
            Pair(Type.OK, Type.OK.msg)
        } else {
            Pair(Type.NOT_FOUND, Type.NOT_FOUND.msg)
        }
    }

    private fun delete(node: TrieNode, key: String, index: Int): Boolean {
        val currentChar = key[index]
        val currentNode = node.subNodes[currentChar]

        if (currentNode!!.subNodes.size > 1 && key.length < index) {
            delete(currentNode, key, index = index + 1)
            return false
        }

        if (index == key.length - 1) {
            return if (currentNode.subNodes.isNotEmpty()) {
                currentNode.fin = false
                currentNode.subNodes.remove('-')
                false
            } else {
                node.subNodes.remove(currentChar)
                true
            }
        }

        if (currentNode.fin) {
            delete(currentNode, key, index = index + 1)
            return false
        }

        val isDeletable = delete(currentNode, key, index = index + 1)
        return if (isDeletable) {
            root.subNodes.remove(currentChar)
            true
        } else false
    }

    private fun buildString(key: String, node: TrieNode, isQuery: Boolean = false): String {
        val builder = StringBuilder().append(key).append(" : { ")
        val separatorNode = node.subNodes['-'] ?: node.subNodes['=']

        if (isQuery) {
            key.replace('.', '-')
        }

        separatorNode?.subNodes?.forEach {
            builder.append(getSubKeys(it.value, it.key, 0))
            builder.append(" ; ")
        }

        return "${builder.trimEnd().dropLast(2)} }"
    }

    private fun getSubKeys(currentNode: TrieNode, char: Char, level: Int): String {
        val builder = StringBuilder()
        currentNode.subNodes.forEach {
            when (char) {
                '-' -> builder.append(" : { ").append(getSubKeys(it.value, it.key, level + 1))
                '=' -> builder.append(" : ").append(getSubKeys(it.value, it.key, level))
                else -> builder.append(char).append(getSubKeys(it.value, it.key, level))
            }
        }

        if (currentNode.subNodes.isEmpty()) {
            builder.append(char)

            for (i in 0 until level) {
                builder.append(" } ")
            }
        }

        return builder.toString()
    }

    private fun exists(key: String): TrieNode? {
        var currentNode = root
        key.forEach {
            val node = currentNode.subNodes[it] ?: return null
            currentNode = node
        }
        return currentNode
    }
}