function indices = find_query_indices(d, query_length)
    fprintf('Creating Query Indices \n ');   
    d1 = [d(1,:);d];
    d2 = [d;d(end,:)];
    d = d1 - d2;
    d = sum(d.*d, 2);
    d = sqrt(d);
%     d(1,:)
%     d(end,:)
    d = d(1:end-1,:);
    d = cumsum(d);
    len = length(d);
    indices = zeros(len,1);
    
    indices(1,1) = 1;
    j=1;    
    for i = 2:len
        if d(i) >= j*query_length
            j = j + 1;
            indices(j,1) = i;
        end
    end
    j
    indices = indices(1:j, 1);
    %[indices d(indices,:)]
    
    fprintf('Creating Query Indices Done\n ');
end