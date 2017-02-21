package net.mamot.bot.commands;

public class TicTacToeCommandTest {


   /* @Test
    public void shouldReturnMessageFromAdviceResource() throws Exception {
        given(new AdviceCommand(new MessageFromURL(resource, new AdvicePrinter()))).
            got("/advice").
        then().
            shouldAnswer(sticker(BLA.id()),
                         message("fucking great advice").disableWebPagePreview(true).parseMode(HTML));
    }

    @Test
    public void shouldReturnApologize_IfAdviceResourceUnreachable() throws Exception {
        when(resource.fetch()).thenThrow(new RuntimeException());

        given(new AdviceCommand(new MessageFromURL(resource, new AdvicePrinter()))).
            got("/advice").
        then().
            shouldAnswer(sticker(ALONE.id()),
                         message("Связь с ноосферой потеряна...").disableWebPagePreview(true).parseMode(HTML));
    }*/
}
