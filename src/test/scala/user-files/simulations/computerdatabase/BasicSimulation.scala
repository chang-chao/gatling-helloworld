// https://gatling.io/docs/current/quickstart/
package computerdatabase // 1.The optional package.

import io.gatling.core.Predef._ // 2.The required imports.
import io.gatling.http.Predef._

class BasicSimulation extends Simulation { // 3. The class declaration. Note that it extends Simulation.

  val httpProtocol = http // 4.The common configuration to all HTTP requests.
    .baseUrl("http://computer-database.gatling.io") // 5. The baseUrl that will be prepended to all relative urls.
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6.Common HTTP headers that will be sent with all the requests.
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("BasicSimulation") // 7.The scenario definition.
    .exec(http("request_1") // 8. A HTTP request, named request_1. This name will be displayed in the final reports.
    .get("/")) // 9. The url this request targets with the GET method.
    .pause(5) // 10. Some pause/think time.

  setUp( // 11. Where one sets up the scenarios that will be launched in this Simulation.
    scn.inject(atOnceUsers(1)) // 12. Declaring to inject into scenario named scn one single user.
  ).protocols(httpProtocol) // 13. Attaching the HTTP configuration declared above.
}