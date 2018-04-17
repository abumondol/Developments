function grid = gridCalculate(data)
grid = zeros(21);

for i = 1:size(data,1)
    g = data{i,1};
    g = int16(g)+11;    
    m = size(g,1);
    for j=1:m
        a = g(j,1);
        b = g(j,2);
        grid(a,b) = grid(a,b) + 1;
    end    
end

grid = grid / max(max(grid));