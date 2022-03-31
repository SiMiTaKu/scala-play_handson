package controllers.tweet

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent
import slick.models.Tweet
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.i18n.I18nSupport
import slick.repositories.TweetRepository
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class TweetFormData(content: String)

@Singleton
class TweetController @Inject()(
    val controllerComponents: ControllerComponents,
    tweetRepository:          TweetRepository
    )(implicit ec: ExecutionContext) extends BaseController with I18nSupport{

  val tweets = scala.collection.mutable.ArrayBuffer((1L to 10L).map(i => Tweet(Some(i), s"test tweet${i.toString}")): _*)

  def list() = Action async{ implicit request: Request[AnyContent] =>
    for {
      results <- tweetRepository.all()
    } yield{
      Ok(views.html.tweet.list(results))
    }
  }

  def show(id: Long) = Action async { implicit request: Request[AnyContent] =>
    for {
      tweetOpt <- tweetRepository.findById(id)
    } yield {
      tweetOpt match {
        case Some(tweet) => Ok(views.html.tweet.show(tweet))
        case None        => NotFound(views.html.error.page404())
      }
    }
  }

  val form = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetFormData.apply)(TweetFormData.unapply)
  )

  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.tweet.store(form))
  }

  def store() = Action async { implicit request: Request[AnyContent] =>
    form.bindFromRequest().fold(
      (formWithErrors: Form[TweetFormData]) =>{
        Future.successful(BadRequest(views.html.tweet.store(formWithErrors)))
      },
      (tweetFormData: TweetFormData) => {
        for {
          _ <- tweetRepository.insert(Tweet(None, tweetFormData.content))
        } yield {
          Redirect(routes.TweetController.list)
        }
      }
    )
  }

  def edit(id:Long) = Action async{ implicit request: Request[AnyContent] =>
    for {
      tweetOpt <- tweetRepository.findById(id)
    } yield {
      tweetOpt match {
        case Some(tweet) =>
          Ok(views.html.tweet.edit(
            id,
            form.fill(TweetFormData(tweet.content))
          ))
        case None =>
          NotFound(views.html.error.page404())
      }
    }
  }

  def update(id:Long) = Action async{ implicit request: Request[AnyContent] =>
    form.bindFromRequest().fold(
      (formWithErrors: Form[TweetFormData]) => {
        Future.successful(BadRequest(views.html.tweet.edit(id, formWithErrors)))
      },
      (data: TweetFormData) => {
        for {
          count <- tweetRepository.updateContent(id, data.content)
        } yield {
          count match {
            case 0 => NotFound(views.html.error.page404())
            case _ => Redirect(routes.TweetController.list)
          }
        }
      }
    )
  }

  def delete() = Action async {implicit request: Request[AnyContent] =>
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    for {
      result <- tweetRepository.delete(idOpt.map(_.toLong))
    } yield {
      result match {
        case 0 => NotFound(views.html.error.page404())
        case _ => Redirect(routes.TweetController.list)
      }
    }
  }
}
