import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Fraud Detection Advanced")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    // Charger dataset
    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("data/fraud_dataset_clear_complete.csv")

    println("=== Aperçu Dataset ===")
    df.show(5)

    // Prétraitement
    val processedDF = Preprocessing.process(df)

    // Split
    val Array(trainDF, testDF) = processedDF.randomSplit(Array(0.8, 0.2), seed = 42)

    // Modèles
    val kmeansDF  = Models.kmeans(trainDF, testDF)
    val classifDF = Models.classification(trainDF, testDF)

    // Évaluation
    println("=== EVALUATION CLASSIFICATION ===")
    Evaluation.evaluate(classifDF)

    spark.stop()
  }
}