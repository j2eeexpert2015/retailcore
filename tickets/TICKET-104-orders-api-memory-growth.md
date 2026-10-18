# TICKET-104 · Incident · Orders.Api memory grows until the container is OOM-killed

**Priority:** High
**Reported by:** On-call (paged twice this month)
**Services suspected:** Orders.Api

## Report

The Orders.Api container's memory climbs steadily under normal traffic and
never comes back down. After roughly two days of production load the
container hits its memory limit and is restarted. Restarts "fix" it, which
is why this has been open for a month.

## Steps to reproduce

1. `docker-compose up -d` and seed
2. Hammer the catalog-reading endpoints with varied product ids for a few
   minutes (any load loop)
3. Watch memory: `docker stats` — Orders.Api only ever grows

## Notes

Heap dumps show one dictionary holding the vast majority of retained
memory. Something is cached and never evicted.
