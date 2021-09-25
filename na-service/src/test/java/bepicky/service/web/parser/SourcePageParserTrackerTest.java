package bepicky.service.web.parser;

import bepicky.service.entity.SourcePage;
import bepicky.service.nats.publisher.AdminMessagePublisher;
import bepicky.service.service.ISourcePageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourcePageParserTrackerTest {

    @InjectMocks
    private SourcePageParserTracker tracker;

    @Mock
    private ISourcePageService spService;

    @Mock
    private AdminMessagePublisher adminMessagePublisher;

    @Test
    public void failed_SourcePageFailedMoreThenLimit_ShouldPublishMessageToAdmin() {
        ReflectionTestUtils.setField(tracker, "sourcePageReadFailLimit", 1);
        Long spId = 1L;
        tracker.failed(spId);

        SourcePage sp = new SourcePage();
        sp.setId(spId);
        sp.setUrl("url");

        when(spService.findById(spId)).thenReturn(Optional.of(sp));
        ArgumentCaptor<String> acParams = ArgumentCaptor.forClass(String.class);
        doNothing().when(adminMessagePublisher).publish(any());

        tracker.failed(spId);

        verify(adminMessagePublisher, times(1)).publish(acParams.capture());

        List<String> params = acParams.getAllValues();

        assertEquals("PAGE EMPTY", params.get(0));
        assertEquals("1", params.get(1));
        assertEquals("url", params.get(2));
        assertEquals("2", params.get(3));
    }

    @Test
    public void failed_SourcePageFailedLessThanLimit_ShouldNotPublishMessageToAdmin() {
        ReflectionTestUtils.setField(tracker, "sourcePageReadFailLimit", 5);
        Long spId = 1L;
        tracker.failed(spId);
        tracker.failed(spId);
        tracker.failed(spId);
        tracker.failed(spId);

        verify(spService, never()).findById(any());
        verify(adminMessagePublisher, never()).publish(any());
    }
}