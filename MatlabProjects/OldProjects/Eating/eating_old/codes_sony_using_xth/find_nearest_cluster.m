function [cluster_index, distance] = find_nearest_cluster(p, C)
    k = size(C,1);
    p = repmat(p, k,1);    
    d = p - C;
    d = sqrt(sum(d.*d, 2));
    [distance, cluster_index] = min(d);
end