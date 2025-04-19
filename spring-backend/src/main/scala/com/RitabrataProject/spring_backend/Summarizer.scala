package com.RitabrataProject.spring_backend

import org.jsoup.Jsoup
import java.net.URL
import java.net.MalformedURLException
import sttp.client3._
import sttp.model._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.DriverManager
import java.time.LocalDateTime

object Summarizer {

  // FastAPI endpoint URL
  val fastApiUrl = "http://localhost:8000/summarize/"

  // Function to summarize the website using the FastAPI endpoint
  def summarize(url: String): Future[String] = {
    try {
      // Validate URL format
      new URL(url)  // This will throw an exception if the URL is malformed

      // Log the received URL for debugging purposes
      println(s"Received valid URL: $url")

      // Fetch the web page using Jsoup to get the content
      val doc = Jsoup.connect(url).get()
      val text = doc.body().text()

      // If text is too long, send the first 500 characters to FastAPI
      if (text.length > 500) {
        summarizeWithFastApi(text.take(500), url)  // Send the first 500 characters to FastAPI for summarization
      } else {
        summarizeWithFastApi(text, url)  // Send the entire text to FastAPI for summarization
      }

    } catch {
      case e: MalformedURLException =>
        // Handle invalid URL format
        Future.successful("Error: Invalid URL format.")
      case e: Exception =>
        // Handle other errors (e.g., network issues)
        Future.successful("Error: Could not fetch the webpage. Please check the URL or try again later.")
    }
  }

  // Function to call FastAPI for summarization
  def summarizeWithFastApi(content: String, url: String): Future[String] = {
    val backend = HttpURLConnectionBackend()

    // Create JSON request body
    val requestBody = s"""{"url": "$content"}"""  // Send the content in the body, as the FastAPI expects a "url" field

    // Send the POST request to FastAPI
    val request = basicRequest
      .body(requestBody)
      .post(uri"$fastApiUrl")
      .contentType("application/json")  // Set content type as application/json

    // Make the request and handle the response asynchronously
    Future {
      val response = request.send(backend)
      response.body match {
        case Right(body) =>
          println(s"Received Summary from FastAPI: $body")

          // Save the summary to the database
          saveSummaryToDb(body, url)
          body // Return the summary from FastAPI
        case Left(error) =>
          println(s"Error from FastAPI: $error")
          s"Error from FastAPI: $error"  // Return the error message from FastAPI
      }
    }
  }

  // Function to save the summary to the database
  def saveSummaryToDb(summary: String, url: String): String = {
    try {
      // Establish database connection
      val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234")
      val statement = connection.prepareStatement("INSERT INTO summarization_history (summary, url, timestamp) VALUES (?, ?, ?)")

      // Set parameters
      statement.setString(1, summary)
      statement.setString(2, url)
      statement.setObject(3, LocalDateTime.now())  // Timestamp

      // Execute the insert statement
      statement.executeUpdate()

      connection.close()
      "Summary saved successfully"
    } catch {
      case e: Exception =>
        e.printStackTrace()
        "Error saving summary"
    }
  }
}
