import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object Models {

  // KMeans (non supervisé)
  def kmeans(trainDF: DataFrame, testDF: DataFrame): DataFrame = {

    val kmeans = new KMeans()
      .setK(2)
      .setSeed(42)
      .setFeaturesCol("features")

    val model = kmeans.fit(trainDF)

    val predictions = model.transform(testDF)

    println("=== KMeans Clusters ===")
    predictions.groupBy("prediction").count().show()

    predictions
  }

  // Classification supervisée (améliorée)
  def classification(trainDF: DataFrame, testDF: DataFrame): DataFrame = {

    // Gestion déséquilibre
    val weightedTrain = trainDF.withColumn("weight",
      when(col("Fraud") === 1, 10.0).otherwise(1.0))

    val lr = new LogisticRegression()
      .setLabelCol("Fraud")
      .setFeaturesCol("features")
      .setWeightCol("weight")
      .setMaxIter(50)

    val model = lr.fit(weightedTrain)

    val predictions = model.transform(testDF)

    println("=== Exemple prédictions ===")
    predictions.select("Fraud", "prediction", "probability").show(10)

    predictions
  }
}