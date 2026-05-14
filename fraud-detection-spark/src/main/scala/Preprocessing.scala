import org.apache.spark.ml.feature.{VectorAssembler, StandardScaler, StringIndexer}
import org.apache.spark.sql.DataFrame

object Preprocessing {

  def process(df: DataFrame): DataFrame = {

    val cleanDF = df.na.drop()

    println("=== Distribution des classes ===")
    cleanDF.groupBy("Fraud").count().show()

    val idx1 = new StringIndexer().setInputCol("TransactionType")
      .setOutputCol("TransactionTypeIdx").setHandleInvalid("keep")
    val idx2 = new StringIndexer().setInputCol("Channel")
      .setOutputCol("ChannelIdx").setHandleInvalid("keep")
    val idx3 = new StringIndexer().setInputCol("DeviceType")
      .setOutputCol("DeviceTypeIdx").setHandleInvalid("keep")

    val df1 = idx1.fit(cleanDF).transform(cleanDF)
    val df2 = idx2.fit(df1).transform(df1)
    val df3 = idx3.fit(df2).transform(df2)

    val featureCols = Array(
      "TransactionAmount", "LoginAttempts", "AccountBalance",
      "TransactionTypeIdx", "ChannelIdx", "DeviceTypeIdx"
    )

    val assembler = new VectorAssembler()
      .setInputCols(featureCols)
      .setOutputCol("features_raw")

    val assembledDF = assembler.transform(df3)

    val scaler = new StandardScaler()
      .setInputCol("features_raw")
      .setOutputCol("features")
      .setWithStd(true)
      .setWithMean(true)

    scaler.fit(assembledDF).transform(assembledDF)
  }
}