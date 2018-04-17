function index = find_closest_cluster(point, centers)
    len = size(centers, 1);
    point = repmat(point, len, 1);
    d = point - centers;
    d = sum(d.*d, 2);
    [~, index] = min(d);    
end