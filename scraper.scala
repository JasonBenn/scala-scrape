import scala.io._
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object Utils {
  def getLength(url : String) = Source.fromURL(url).mkString.length

  val tasks = List("https://www.google.com/",
                   "https://www.twitter.com/",
                   "https://www.facebook.com/")

  def benchmark(method : List[String] => Unit) = {
    val start = System.nanoTime()
    method(tasks)
    val end = System.nanoTime()
    println("Method took " + (end - start)/1000000000.0 + " seconds" )
  }
}

class PageActor(task : String) extends Actor {
  def receive = {
    case (url : String) =>
      println("my job is: " + task)
      println(Utils.getLength(url))
  }
}

object Main extends App {

  val system = ActorSystem("FirstSystem")
  val firstActor = system.actorOf(Props(new PageActor("google!")), name = "firstactor")

  def concurrent(tasks: List[String]) = {
    var i = 1
    tasks foreach {(task : String) =>
      i += 1
      system.actorOf(Props(new PageActor("task")), name="task" + i) ! task
    }
  }

  def synchronous(tasks : List[String]) = {
    tasks foreach (firstActor ! _)
  }

  Utils.benchmark { synchronous }
  Utils.benchmark { concurrent }
}
