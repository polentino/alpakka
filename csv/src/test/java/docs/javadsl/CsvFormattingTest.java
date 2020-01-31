/*
 * Copyright (C) 2016-2020 Lightbend Inc. <https://www.lightbend.com>
 */

package docs.javadsl;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
// #import
import akka.stream.alpakka.csv.javadsl.CsvFormatting;
import akka.stream.alpakka.csv.javadsl.CsvQuotingStyle;

// #import
import akka.stream.alpakka.testkit.javadsl.LogCapturingJunit4;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.testkit.javadsl.StreamTestKit;
import akka.testkit.javadsl.TestKit;
import akka.util.ByteString;
import org.junit.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CsvFormattingTest {
  @Rule public final LogCapturingJunit4 logCapturing = new LogCapturingJunit4();

  private static ActorSystem system;
  private static Materializer materializer;

  public void documentation() {
    char delimiter = CsvFormatting.COMMA;
    char quoteChar = CsvFormatting.DOUBLE_QUOTE;
    char escapeChar = CsvFormatting.BACKSLASH;
    String endOfLine = CsvFormatting.CR_LF;
    Charset charset = StandardCharsets.UTF_8;
    Optional<ByteString> byteOrderMark = Optional.empty();
    // #flow-type
    Flow<Collection<String>, ByteString, ?> flow1 = CsvFormatting.format();

    Flow<Collection<String>, ByteString, ?> flow2 =
        CsvFormatting.format(
            delimiter,
            quoteChar,
            escapeChar,
            endOfLine,
            CsvQuotingStyle.REQUIRED,
            charset,
            byteOrderMark);
    // #flow-type
  }

  @Test
  public void standardCsvFormatShouldWork() {
    CompletionStage<ByteString> completionStage =
        // #formatting
        Source.single(Arrays.asList("one", "two", "three", "four"))
            .via(CsvFormatting.format())
            .runWith(Sink.head(), materializer);
    // #formatting
    completionStage.thenAccept((bs) -> assertThat(bs.utf8String(), equalTo("one,two,three")));
  }

  @BeforeClass
  public static void setup() throws Exception {
    system = ActorSystem.create();
    materializer = ActorMaterializer.create(system);
  }

  @AfterClass
  public static void teardown() throws Exception {
    TestKit.shutdownActorSystem(system);
  }

  @After
  public void checkForStageLeaks() {
    StreamTestKit.assertAllStagesStopped(materializer);
  }
}
