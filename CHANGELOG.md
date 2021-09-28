# Klogging changes

## Version 0.4.0

- New architecture with multiple coroutine channels for handling log events.
- Batching events to send to sink destinations.
- Configure with `sendTo` instead of `dispatchTo`.
- Sink for sending log events to Splunk servers.
- More comprehensive diagnostics using internal logger.
- Coloured, column-aligned output in console renderer.
