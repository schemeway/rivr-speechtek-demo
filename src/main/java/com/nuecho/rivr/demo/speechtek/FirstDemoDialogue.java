/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */

package com.nuecho.rivr.demo.speechtek;

import static com.nuecho.rivr.core.dialogue.DialogueUtils.*;
import static com.nuecho.rivr.voicexml.turn.output.interaction.InteractionBuilder.*;

import com.nuecho.rivr.core.util.*;
import com.nuecho.rivr.voicexml.dialogue.*;
import com.nuecho.rivr.voicexml.rendering.voicexml.*;
import com.nuecho.rivr.voicexml.turn.first.*;
import com.nuecho.rivr.voicexml.turn.input.*;
import com.nuecho.rivr.voicexml.turn.last.*;
import com.nuecho.rivr.voicexml.turn.output.*;
import com.nuecho.rivr.voicexml.turn.output.audio.*;
import com.nuecho.rivr.voicexml.turn.output.grammar.*;
import com.nuecho.rivr.voicexml.turn.output.interaction.*;

/**
 * @author Nu Echo Inc.
 */
public final class FirstDemoDialogue implements VoiceXmlDialogue {

    @Override
    public VoiceXmlLastTurn run(VoiceXmlFirstTurn firstTurn, VoiceXmlDialogueContext context) throws Exception {
        SpeechRecognitionConfiguration recConfig = new SpeechRecognitionConfiguration(new GrammarReference("builtin:grammar/number"));

        InteractionTurn question = newInteractionBuilder("question").addPrompt(new SynthesisText("Say a number."))
                                                                    .build(recConfig, TimeValue.seconds(5));
        VoiceXmlInputTurn answer = doTurn(context, question);

        int number = Integer.parseInt(answer.getRecognitionInfo()
                                            .getRecognitionResult()
                                            .getJsonObject(0)
                                            .getString("interpretation"));

        if (number >= 1000) {
            doTurn(context, new MessageTurn("feedback-large", new SynthesisText("That's a big number")));
        } else {
            doTurn(context, new MessageTurn("feedback-small", new SynthesisText("That's a reasonable number")));
        }

        return new VoiceXmlExitTurn("exit");
    }
}
