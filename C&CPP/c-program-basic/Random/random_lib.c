static unsigned long int next = 1;  /* the seed  */

int rand1(void)
{
    /* magic formula to generate pseudorandom number */
    /* 生成伪随机数的魔术公式 */
    next = next * 1103515245 + 12345;
    return (unsigned int) (next/65536) % 32768;
}

void srand1(unsigned int seed)
{
    next = seed;
}
