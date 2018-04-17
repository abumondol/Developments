function weights = gridWeights(grid, data, indices, axes)
len = size(indices,1);
weights = zeros(len,1);
for i = 1:len
    s = indices(i,1);
    e = indices(i,2); 
    window = data(s:e, :);
    window = window(window(:,2)==9, axes);
    weights(i,1) = gridWeightSingleWindow(grid, window);
end

end

function weight = gridWeightSingleWindow(grid, window)
    window = int16(window) + 11;
    len = size(window, 1);
    weight = 0;
    for i=1:len
        a = window(i,1);
        b= window(i,2);
        weight = weight + grid(a, b);
    end
    weight = weight/len;    
end