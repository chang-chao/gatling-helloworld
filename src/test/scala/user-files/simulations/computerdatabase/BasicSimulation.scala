// https://gatling.io/docs/current/quickstart/
package computerdatabase // 1.The optional package.

import java.nio.charset.StandardCharsets

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.language.postfixOps
import scala.util.Random
import scala.concurrent.duration._

class BasicSimulation extends Simulation { // 3. The class declaration. Note that it extends Simulation.

  val httpProtocol = http // 4.The common configuration to all HTTP requests.
    .baseUrl("http://httpbin.org/") // 5. The baseUrl that will be prepended to all relative urls.
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6.Common HTTP headers that will be sent with all the requests.
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("conditional test") // 7.The scenario definition.
    .exec(_ set("key", Random.nextDouble()))
    .exec(
      http("get some data") // 8. A HTTP request, This name will be displayed in the final reports.
        .get("get")
        .queryParam("key", "${key}") //  Gatling EL:https://gatling.io/docs/current/session/expression_el
        .check(jsonPath("$.args.key") // https://gatling.io/docs/current/http/http_check
        .saveAs("response_key")))
    // https://gatling.io/docs/current/general/scenario
    // Conditional statements¶
    .doIf(_ ("response_key").as[String].toDouble < 0.5) {
    exec(http("post some data when some condition met")
      .post("/post")
      .header("Content-Type", "application/json")
      .body(ElFileBody("user-files/resources/httpbin.post.json"))
      // https://stackoverflow.com/questions/56664382/how-to-read-body-as-string-before-sending-request-in-gatling
      .sign(new SignatureCalculator {
      override def sign(request: Request): Unit = {
        val bodyStr = request.getBody().getBytes;
        val str = new String(bodyStr, StandardCharsets.UTF_8)
        // https://stackoverflow.com/questions/57238382/how-to-generate-a-hmac-signature-using-common-module-gatling3-1
        request.getHeaders.add("Authorization", "foo")
      }
    })
      // https://gatling.io/docs/current/http/http_request/
      .processRequestBody({ body => {
      //  println(body.toString)
      body
    }
    })

      .check(status is 200)
    )
  }
  //    .pause(5) // 10. Some pause/think time.

  setUp( // 11. Where one sets up the scenarios that will be launched in this Simulation.
    scn.inject(
      // https://gatling.io/docs/current/general/simulation_setup
      rampUsersPerSec(1) to 2 during (2 seconds),
      constantUsersPerSec(2) during (5 seconds),
    ) // 12. Declaring to inject into scenario named scn one single user.
  ).protocols(httpProtocol) // 13. Attaching the HTTP configuration declared above.
}