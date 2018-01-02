source("../../utils/mongo_utils.R")

print_thumbs_distribution <- function(data, thumbs){
  data %>% ggplot(aes_string(thumbs)) + geom_bar()
}

get_boundary <- function(data, thumbs){
  rated_comments = data %>% filter(get(thumbs) > 0)
  quantiles = rated_comments[,thumbs] %>% quantile()
  bound = quantiles[names(quantiles) == "75%"]
  return(bound)  
}

slice_data_by_one_bound <- function(data, bound, thumbs){
  data %>% filter(get(thumbs) >= bound)
}

get_comments_by_one_boundary <- function(data, thumbs){
  bound = get_boundary(data, thumbs)
  sliced_data = slice_data_by_one_bound(data, bound, thumbs)
  return(sliced_data)
}

get_comments_by_both_boundaries <- function(data){
  boundUp = get_boundary(data, "ThumbsUp")
  boundDown = get_boundary(data, "ThumbsDown")
  data %>% filter(ThumbsUp >= boundUp, ThumbsDown >= boundDown)
}
