package org.scalatest;

object TestObject {
	def main(args: Array[String]) {
		println("Hello, world!!!")
	}
	
	def getMsg() = "From org.scalatest.TestObject: Hello there!!!"
}

class TestClass {
	def getMsg() = "From org.scalatest.TestClass: Hello there!!!"
}