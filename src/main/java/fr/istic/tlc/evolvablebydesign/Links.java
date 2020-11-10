package fr.istic.tlc.evolvablebydesign;

import fr.istic.tlc.domain.Poll;

public interface Links {

  static HypermediaRepresentation<Poll> withLinks(Poll poll) {
    return HypermediaRepresentation.of(poll)
      .withLink("update")
      .withLink("comment")
      .withLink("vote")
      .withLink("listComments")
      .build();
  }

}
