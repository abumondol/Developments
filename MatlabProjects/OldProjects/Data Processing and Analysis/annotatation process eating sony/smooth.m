function res =smooth(data, alpha)
[row_count, col_count] = size(data);
res = zeros(row_count, col_count);
res(1,:) = data(1,:);

for j=2:row_count
    res(j,:) = alpha*res(j-1,:) + (1-alpha)*data(j,:);
end

end