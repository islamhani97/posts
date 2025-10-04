package com.islam97.android.apps.posts.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.islam97.android.apps.posts.domain.model.Comment

@Composable
fun CommentItem(
    modifier: Modifier = Modifier, comment: Comment
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val (avatarReference, nameReference, bodyReference) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(avatarReference) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.name.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            modifier = Modifier
                .padding(start = 12.dp)
                .constrainAs(nameReference) {
                    top.linkTo(avatarReference.top)
                    start.linkTo(avatarReference.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(avatarReference.bottom)
                    width = Dimension.fillToConstraints
                },
            text = comment.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 12.dp)
                .constrainAs(bodyReference) {
                    top.linkTo(nameReference.bottom)
                    start.linkTo(avatarReference.end)
                    end.linkTo(nameReference.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            text = comment.body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentItem() {
    CommentItem(comment = Comment(postId = 0, id = 0, name = "Name", email = "", body = "Body"))
}