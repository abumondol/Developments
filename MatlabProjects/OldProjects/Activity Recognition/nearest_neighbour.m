function [min_dis, nn_index] = nearest_neighbour(window, dict)
len = length(dict);
min_dis = Inf;
nn_index = 1;

for i=1:len   
    
    distance = DTW(window, dict(i).data);
    if distance < min_dis
        min_dis = distance;
        nn_index = i;
    end
end


end
