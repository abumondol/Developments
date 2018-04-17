sub_count = length(data);

segments = [];
for s =1:sub_count
    x = data(s).accel(:, 2);    
    x_count = length(x);
    all_seg_count = floor(x_count/w);
    all_segs = zeros(all_seg_count, 2);
    selected = zeros(all_seg_count, 1);
    
    
    for i=1:all_seg_count
        si = (i-1)*w+1;
        ei = i*w;
        [m, I] = min(x(si:ei));
        
        all_segs(i,1) = si+I-1;        
        all_segs(i,2) = m;
        %all_segs(i,3) = si;
        %all_segs(i,4) = ei;
    end
    
    for i=6:all_seg_count-5
        if all_segs(i,2) < all_segs(i-1, 2) && all_segs(i,2) <= all_segs(i+1, 2) && all_segs(i,2)<=min_x
            selected(i) = 1;
        end
    end
    
    segments(s).all_min_points = all_segs; 
    segments(s).segs_by_minx = all_segs(selected==1, :); 
end

save('segments', 'segments');
fprintf('Find Min Points done\n');
