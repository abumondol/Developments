function   p = get_paramters()
    p = struct;
    p.x_th = -3.5;
    p.step_size = 0.25;
    p.max_chunk_size = 3;
    p.min_chunk_size = .5;
    
%     fprintf('PARAMETERS:\n');
%     fprintf('-----------\n');
%     fprintf('x_th: %d\n', p.x_th);
%     fprintf('step_size: %d\n', p.step_size);
%     fprintf('max_chunk_size: %d\n', p.max_chunk_size);    
%     fprintf('\n');    
end