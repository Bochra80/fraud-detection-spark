import org.apache.spark.ml.evaluation._
import org.apache.spark.sql.DataFrame

object Evaluation {

  def evaluate(df: DataFrame): Unit = {

    println("=== MATRICE DE CONFUSION ===")
    df.groupBy("Fraud", "prediction").count().show()

    val precision = new MulticlassClassificationEvaluator()
      .setLabelCol("Fraud")
      .setPredictionCol("prediction")
      .setMetricName("weightedPrecision")
      .evaluate(df)

    val recall = new MulticlassClassificationEvaluator()
      .setMetricName("weightedRecall")
      .setLabelCol("Fraud")
      .setPredictionCol("prediction")
      .evaluate(df)

    val f1 = new MulticlassClassificationEvaluator()
      .setMetricName("f1")
      .setLabelCol("Fraud")
      .setPredictionCol("prediction")
      .evaluate(df)

    val auc = new BinaryClassificationEvaluator()
      .setLabelCol("Fraud")
      .setRawPredictionCol("rawPrediction")
      .evaluate(df)

    println(s"Precision = $precision")
    println(s"Recall = $recall")
    println(s"F1 Score = $f1")
    println(s"AUC = $auc")
  }
}