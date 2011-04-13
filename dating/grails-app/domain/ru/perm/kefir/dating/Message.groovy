package ru.perm.kefir.dating

class Message {
    static mapping = {
        version false;
    }

    Profile from;
    Profile to;
    String text;
    Date date;

    static belongsTo = [from: Profile, to: Profile];

    static constraints = {
        from(nullable: false);
        to(nullable: false);
        text(nullable: false, blank: false, size: 1..511);
        date(nullable: false);
    }

    /**
     * Get last messages
     */
    static List<Message> last(Profile p1, Profile p2, int max) {
        return withCriteria {
            or {
                and {
                    eq('from', p1);
                    eq('to', p2);
                }
                and {
                    eq('from', p2);
                    eq('to', p1);
                }
            }
            order('date', 'desc');
            maxResults(max);
        }.reverse();
    }

    /**
     * Get all messages after timestamp
     */
    static List<Message> after(Profile p1, Profile p2, long timestamp) {
        return withCriteria {
            or {
                and {
                    eq('from', p1);
                    eq('to', p2);
                }
                and {
                    eq('from', p2);
                    eq('to', p1);
                }
            }
            gt('date', new Date(timestamp));
            order('date', 'desc');
        }.reverse();
    }

    /**
     * Get some messages before timestamp
     */
    static List<Message> before(Profile p1, Profile p2, long timestamp, int max){
        return withCriteria {
            or {
                and {
                    eq('from', p1);
                    eq('to', p2);
                }
                and {
                    eq('from', p2);
                    eq('to', p1);
                }
            }
            lt('date', new Date(timestamp));
            order('date', 'desc');
            maxResults(max);
        };
    }
}
