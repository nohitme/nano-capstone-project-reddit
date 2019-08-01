package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SwipedPostEntity {

  @PrimaryKey
  @NonNull
  public String id;
}
