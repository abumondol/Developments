function draw_dtw_mappings()
    load('signatures');
    load('grid');
    draw_dtw_mapping(signatures(1), signatures(2), airsign_grid(1,2).dtw_paths, 1, 1);
    draw_dtw_mapping(signatures(1), signatures(2), airsign_grid(1,2).dtw_paths, 1, 2);
    draw_dtw_mapping(signatures(1), signatures(2), airsign_grid(1,2).dtw_paths, 1, 3);
    draw_dtw_mapping(signatures(1), signatures(2), airsign_grid(1,2).dtw_paths, 1, 4);
end