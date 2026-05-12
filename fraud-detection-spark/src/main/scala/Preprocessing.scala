import org.apache.spark.ml.feature.{VectorAssembler, StandardScaler}
import org.apache.spark.sql.DataFrame

object Preprocessing {

  def process(df: DataFrame): DataFrame = {

    // Nettoyage
    val cleanDF = df.na.drop()

    println("=== Distribution des classes ===")
    cleanDF.groupBy("label").count().show()

    // Sélection features
    val featureCols = cleanDF.columns.filter(_ != "label")

    val assembler = new VectorAssembler()
      .setInputCols(featureCols)
      .setOutputCol("features_raw")

    val assembledDF = assembler.transform(cleanDF)

    // Normalisation
    val scaler = new StandardScaler()
      .setInputCol("features_raw")
      .setOutputCol("features")
      .setWithStd(true)
      .setWithMean(true)

    val scalerModel = scaler.fit(assembledDF)

    scalerModel.transform(assembledDF)
  }
}